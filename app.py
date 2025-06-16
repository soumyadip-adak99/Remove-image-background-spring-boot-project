import io
from flask import Flask, request, send_file, jsonify
from rembg import remove
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
        if file.filename == '':
            return jsonify({"error": "No selected file"}), 400

        # Check file extension
        allowed_extensions = {'png', 'jpg', 'jpeg', 'webp'}
        if not ('.' in file.filename and file.filename.rsplit('.', 1)[1].lower() in allowed_extensions):
            return jsonify({"error": "Invalid file type"}), 400

        input_image = file.read()
        output_image = remove(input_image)

        return send_file(
            io.BytesIO(output_image),
            mimetype="image/png",
            as_attachment=False
        )

    except Exception as e:
        app.logger.error(f"Error while removing background: {str(e)}")
        return jsonify({"error": "Internal server error"}), 500


@app.errorhandler(413)
def file_too_large(e):
    return jsonify({"error": "File too large. Max size is 20MB."}), 413


if __name__ == "__main__":
    app.run(host='0.0.0.0', port=8000)