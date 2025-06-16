import io
from flask import Flask, request, send_file, jsonify
from rembg import remove
import logging

app = Flask(__name__)

# Configuration
app.config['MAX_CONTENT_LENGTH'] = 20 * 1024 * 1024  # 20MB
PORT = 10000  # Explicit port for Render compatibility

# Logging
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)


@app.route("/")
def health_check():
    """Endpoint for health checks and port verification"""
    return "Background Removal API is running", 200


@app.route("/remove-background", methods=["POST"])
def remove_background():
    if 'file' not in request.files:
        return jsonify({"error": "No file provided"}), 400

    file = request.files['file']
    if not file or file.filename == '':
        return jsonify({"error": "No selected file"}), 400

    if not allowed_file(file.filename):
        return jsonify({"error": "Invalid file type. Allowed: png, jpg, jpeg, webp"}), 400

    try:
        output_image = remove(file.read())
        return send_file(io.BytesIO(output_image), mimetype="image/png")
    except Exception as e:
        logger.error(f"Background removal failed: {str(e)}")
        return jsonify({"error": "Processing failed"}), 500


def allowed_file(filename):
    return '.' in filename and \
        filename.rsplit('.', 1)[1].lower() in {'png', 'jpg', 'jpeg', 'webp'}


if __name__ == "__main__":
    # When running with gunicorn, this won't be executed
    app.run(host='0.0.0.0', port=PORT)