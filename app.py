import io
from flask import Flask, request, send_file, jsonify
from rembg import remove
import logging

# Minimal Flask app setup
app = Flask(__name__)
app.config['MAX_CONTENT_LENGTH'] = 20 * 1024 * 1024  # 20MB

# Lightweight logging
logging.basicConfig(level=logging.WARNING)  # Reduced from INFO to WARNING
logger = logging.getLogger('bg_removal')

# Optimized allowed file check
ALLOWED_EXTENSIONS = {'png', 'jpg', 'jpeg', 'webp'}


@app.route("/")
def health_check():
    return "API Ready", 200


@app.route("/process", methods=["POST"])  # Shorter endpoint
def process_image():
    if 'file' not in request.files:
        return jsonify({"error": "File missing"}), 400

    file = request.files['file']
    if not file or not file.filename:
        return jsonify({"error": "Invalid file"}), 400

    if not ('.' in file.filename and
            file.filename.rsplit('.', 1)[1].lower() in ALLOWED_EXTENSIONS):
        return jsonify({"error": "File type not allowed"}), 400

    try:
        # Stream processing instead of full read when possible
        img_bytes = file.read()
        if len(img_bytes) > app.config['MAX_CONTENT_LENGTH']:
            return jsonify({"error": "File too large"}), 413

        # Process and return in one go
        return send_file(
            io.BytesIO(remove(img_bytes)),
            mimetype="image/png",
            as_attachment=False
        )
    except Exception as e:
        logger.warning(f"Processing failed: {str(e)}")  # Reduced logging
        return jsonify({"error": "Processing error"}), 500


if __name__ == "__main__":
    app.run(host='0.0.0.0', port=8000)  # Standard port