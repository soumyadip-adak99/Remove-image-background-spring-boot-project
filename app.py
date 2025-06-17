import io
import os
import gc
import logging
from flask import Flask, request, send_file, jsonify
from rembg import remove, new_session
from PIL import Image, ImageOps
import psutil

app = Flask(__name__)

app.config.update(
    MAX_CONTENT_LENGTH=20 * 1024 * 1024,
    SEND_FILE_MAX_AGE_DEFAULT=3600,
    JSON_SORT_KEYS=False
)

logging.basicConfig(
    level=logging.ERROR,
    format='%(asctime)s - %(levelname)s - %(message)s'
)
logger = logging.getLogger(__name__)

ALLOWED_EXTENSIONS = frozenset(['png', 'jpg', 'jpeg', 'webp'])
MAX_DIMENSION = 2048

MODEL_NAME = os.getenv("REMBG_MODEL", "isnet-general-use")

try:
    rembg_session = new_session(MODEL_NAME)
    logger.info(f"Rembg session initialized with model: {MODEL_NAME}")
except Exception as e:
    logger.error(f"Failed to initialize rembg session with model '{MODEL_NAME}': {e}")
    rembg_session = None


def is_allowed_file(filename):
    return '.' in filename and \
        filename.rsplit('.', 1)[1].lower() in ALLOWED_EXTENSIONS


def optimize_image(img_bytes):
    try:
        with Image.open(io.BytesIO(img_bytes)) as img:
            if img.width > MAX_DIMENSION or img.height > MAX_DIMENSION:
                img.thumbnail((MAX_DIMENSION, MAX_DIMENSION), Image.Resampling.LANCZOS)

            if img.mode != 'RGBA':
                img = img.convert('RGBA')

            output = io.BytesIO()
            img.save(output, format='PNG', optimize=True, compress_level=6)
            return output.getvalue()
    except Exception as e:
        logger.error(f"Image optimization failed: {e}")
        return img_bytes


@app.route("/", methods=["GET"])
def health_check():
    try:
        memory_usage = psutil.virtual_memory().percent
        return jsonify({
            "status": "healthy",
            "memory_usage": f"{memory_usage:.1f}%",
            "rembg_model": MODEL_NAME,
            "rembg_ready": rembg_session is not None
        }), 200
    except Exception as e:
        logger.error(f"Health check failed: {e}")
        return jsonify({"status": "unhealthy", "message": str(e)}), 500


@app.route("/remove-bg", methods=["POST"])
def remove_background():
    if 'file' not in request.files:
        return jsonify({"error": "No file provided"}), 400

    file = request.files['file']
    if not file or not file.filename:
        return jsonify({"error": "Invalid file"}), 400

    if not is_allowed_file(file.filename):
        return jsonify({
            "error": "File type not supported",
            "supported": list(ALLOWED_EXTENSIONS)
        }), 400

    try:
        img_bytes = file.read()
        if len(img_bytes) > app.config['MAX_CONTENT_LENGTH']:
            max_mb = app.config['MAX_CONTENT_LENGTH'] / (1024 * 1024)
            return jsonify({"error": f"File too large (max {max_mb:.0f}MB)"}), 413

        optimized_img_bytes = optimize_image(img_bytes)

        if rembg_session is None:
            return jsonify({"error": "Service temporarily unavailable. Model not loaded."}), 503

        result_bytes = remove(
            optimized_img_bytes,
            session=rembg_session,
            alpha_matting=True,
            alpha_matting_foreground_threshold=240,
            alpha_matting_background_threshold=10,
            alpha_matting_erode_size=10,
        )

        gc.collect()

        return send_file(
            io.BytesIO(result_bytes),
            mimetype="image/png",
            as_attachment=False,
            download_name="background_removed.png"
        )

    except MemoryError:
        gc.collect()
        logger.error("Out of memory processing image", exc_info=True)
        return jsonify({"error": "Image too large or complex to process (out of memory)"}), 413
    except ValueError as e:
        logger.error(f"Invalid image file format or corrupted: {e}", exc_info=True)
        return jsonify({"error": "Invalid image file format or corrupted"}), 400
    except Exception as e:
        gc.collect()
        logger.error(f"Processing failed for file: {file.filename} - Error: {str(e)}", exc_info=True)
        return jsonify({"error": "Failed to process image. Please try again."}), 500


@app.route("/health", methods=["GET"])
def detailed_health():
    try:
        mem = psutil.virtual_memory()
        return jsonify({
            "status": "ok",
            "memory": {
                "total": f"{mem.total / (1024 ** 3):.2f}GB",
                "used": f"{mem.used / (1024 ** 3):.2f}GB",
                "percent": f"{mem.percent:.1f}%"
            },
            "rembg_model_loaded": MODEL_NAME,
            "rembg_session_status": "ready" if rembg_session else "unavailable"
        })
    except Exception as e:
        logger.error(f"Detailed health check failed: {e}", exc_info=True)
        return jsonify({"status": "error", "message": str(e)}), 500


@app.errorhandler(413)
def request_entity_too_large(error):
    max_mb = app.config['MAX_CONTENT_LENGTH'] / (1024 * 1024)
    return jsonify({"error": f"File too large (max {max_mb:.0f}MB)"}), 413


@app.errorhandler(500)
def internal_error(error):
    gc.collect()
    logger.error(f"Unhandled internal server error: {error}", exc_info=True)
    return jsonify({"error": "Internal server error. Please contact support."}), 500


@app.teardown_appcontext
def cleanup(error):
    gc.collect()


if __name__ == "__main__":
    port = int(os.getenv("PORT", 8080))
    print(f"Starting Flask app on port {port}")
    app.run(host='0.0.0.0', port=port, debug=False)