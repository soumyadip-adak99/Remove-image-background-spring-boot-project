# Use an official slim Python image compatible with rembg
FROM python:3.10-slim

# Set the working directory
WORKDIR /app

# Copy the application files into the container
COPY . .

# Upgrade pip and install dependencies
RUN pip install --upgrade pip && pip install -r requirements.txt

# Expose the port Flask will run on
EXPOSE 5000

# Start the Flask app using Gunicorn
CMD ["gunicorn", "app:app", "--bind", "0.0.0.0:5000"]
