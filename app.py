import io
import os
from flask import Flask, request, send_file, jsonify
from rembg import remove
from PIL import Image
import logging

app = Flask(__name__)

# Set max file size to 20 MB
app.config['MAX_CONTENT_LENGTH'] = 20 * 1024 * 1024  # 20MB

logging.basicConfig(level=logging.INFO)

@app.route("/", methods=["GET"])
def index():
    return "Background Removal API is running."

@app.route("/remove-background", methods=["POST"])
def remove_background():
    if 'file' not in request.files:
        return jsonify({"error": "No file provided"}), 400

    try:
        file = request.files['file']
        input_image = file.read()

        output_image = remove(input_image)

        return send_file(
            io.BytesIO(output_image),
            mimetype="image/png",
            as_attachment=False
        )

    except Exception as e:
        app.logger.exception("Error while removing background")
        return jsonify({"error": str(e)}), 500

@app.errorhandler(413)
def file_too_large(e):
    return jsonify({"error": "File too large. Max size is 20MB."}), 413

if __name__ == "__main__":
    # Use the PORT environment variable provided by Render, defaulting to 8000
    port = int(os.getenv("PORT", 8000))
    app.run(host='0.0.0.0', port=port, debug=False)