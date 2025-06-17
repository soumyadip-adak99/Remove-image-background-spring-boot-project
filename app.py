import io
import os
import gc
import logging
from flask import Flask, request, send_file, jsonify
from rembg import remove, new_session
from PIL import Image
import psutil

# Production Flask app setup
app = Flask(__name__)
app.config.update(
    MAX_CONTENT_LENGTH=15 * 1024 * 1024,  # 15MB limit
    SEND_FILE_MAX_AGE_DEFAULT=3600,  # Cache for 1 hour
    JSON_SORT_KEYS=False
)

# Optimized logging for production
logging.basicConfig(
    level=logging.ERROR,  # Only log errors
    format='%(asctime)s - %(levelname)s - %(message)s'
)
logger = logging.getLogger(__name__)

# File validation
ALLOWED_EXTENSIONS = frozenset(['png', 'jpg', 'jpeg', 'webp'])
MAX_DIMENSION = 2048  # Max width/height

# Initialize rembg session once (memory optimization)
try:
    # Use u2net model (smaller than u2net_human_seg)
    rembg_session = new_session('u2net')
except Exception as e:
    logger.error(f"Failed to initialize rembg session: {e}")
    rembg_session = None


def is_allowed_file(filename):
    """Fast file extension check"""
    return '.' in filename and \
        filename.rsplit('.', 1)[1].lower() in ALLOWED_EXTENSIONS


def optimize_image(img_bytes, max_size=15 * 1024 * 1024):
    """Optimize image size and dimensions"""
    try:
        with Image.open(io.BytesIO(img_bytes)) as img:
            # Check and resize if too large
            if img.width > MAX_DIMENSION or img.height > MAX_DIMENSION:
                img.thumbnail((MAX_DIMENSION, MAX_DIMENSION), Image.Resampling.LANCZOS)

            # Convert to RGB if necessary (for JPEG compatibility)
            if img.mode in ('RGBA', 'LA', 'P'):
                # Keep RGBA for PNG output
                pass
            elif img.mode != 'RGB':
                img = img.convert('RGB')

            # Save optimized image
            output = io.BytesIO()
            img.save(output, format='PNG', optimize=True, compress_level=6)
            return output.getvalue()
    except Exception as e:
        logger.error(f"Image optimization failed: {e}")
        return img_bytes


@app.route("/", methods=["GET"])
def health_check():
    """Health check endpoint"""
    try:
        memory_usage = psutil.virtual_memory().percent
        return jsonify({
            "status": "healthy",
            "memory_usage": f"{memory_usage:.1f}%",
            "rembg_ready": rembg_session is not None
        }), 200
    except:
        return jsonify({"status": "healthy"}), 200


@app.route("/remove-bg", methods=["POST"])
def remove_background():
    """Remove background from uploaded image"""

    # Validate request
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
        # Read and validate file size
        img_bytes = file.read()
        if len(img_bytes) > app.config['MAX_CONTENT_LENGTH']:
            return jsonify({"error": "File too large (max 15MB)"}), 413

        # Optimize image before processing
        img_bytes = optimize_image(img_bytes)

        # Check if rembg session is available
        if rembg_session is None:
            return jsonify({"error": "Service temporarily unavailable"}), 503

        # Process image
        result = remove(img_bytes, session=rembg_session)

        # Force garbage collection to free memory
        gc.collect()

        # Return processed image
        return send_file(
            io.BytesIO(result),
            mimetype="image/png",
            as_attachment=False,
            download_name="background_removed.png"
        )

    except MemoryError:
        gc.collect()  # Force cleanup
        logger.error("Out of memory processing image")
        return jsonify({"error": "Image too large to process"}), 413

    except Exception as e:
        gc.collect()  # Force cleanup on error
        logger.error(f"Processing failed: {str(e)}")
        return jsonify({"error": "Failed to process image"}), 500


@app.route("/health", methods=["GET"])
def detailed_health():
    """Detailed health check for monitoring"""
    try:
        mem = psutil.virtual_memory()
        return jsonify({
            "status": "ok",
            "memory": {
                "total": f"{mem.total / (1024 ** 3):.2f}GB",
                "used": f"{mem.used / (1024 ** 3):.2f}GB",
                "percent": f"{mem.percent:.1f}%"
            },
            "rembg_session": "ready" if rembg_session else "unavailable"
        })
    except Exception as e:
        return jsonify({"status": "error", "message": str(e)}), 500


@app.errorhandler(413)
def request_entity_too_large(error):
    return jsonify({"error": "File too large (max 15MB)"}), 413


@app.errorhandler(500)
def internal_error(error):
    gc.collect()  # Cleanup on server error
    return jsonify({"error": "Internal server error"}), 500


# Cleanup on app teardown
@app.teardown_appcontext
def cleanup(error):
    gc.collect()


if __name__ == "__main__":
    port = int(os.environ.get("PORT", 8080))
    debug = os.environ.get("FLASK_ENV", "production") == "development"

    app.run(
        host="0.0.0.0",
        port=port,
        debug=debug,
        threaded=True  # Handle multiple requests
    )