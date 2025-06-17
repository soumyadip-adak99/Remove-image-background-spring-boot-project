# Use Python 3.11 slim for minimal size
FROM python:3.11-slim

# Set environment variables for Python optimization
ENV PYTHONUNBUFFERED=1 \
    PYTHONDONTWRITEBYTECODE=1 \
    PIP_NO_CACHE_DIR=1 \
    PIP_DISABLE_PIP_VERSION_CHECK=1 \
    DEBIAN_FRONTEND=noninteractive \
    FLASK_ENV=production

# Update and install only essential system dependencies
RUN apt-get update && apt-get install -y --no-install-recommends \
    # Essential libraries for image processing
    libglib2.0-0 \
    libsm6 \
    libxext6 \
    libxrender-dev \
    libgomp1 \
    libgl1-mesa-glx \
    libgthread-2.0-0 \
    # Cleanup in same layer to reduce size
    && rm -rf /var/lib/apt/lists/* \
    && apt-get clean \
    && apt-get autoremove -y

# Create app directory and user
RUN useradd --create-home --shell /bin/bash --user-group app
WORKDIR /app

# Copy requirements first for better Docker layer caching
COPY requirements.txt .

# Install Python dependencies with size optimizations
RUN pip install --no-cache-dir --upgrade pip \
    # Install PyTorch CPU-only version first (much smaller)
    && pip install torch torchvision --index-url https://download.pytorch.org/whl/cpu \
    # Install other requirements
    && pip install --no-cache-dir -r requirements.txt \
    # Pre-download rembg models to reduce runtime
    && python -c "from rembg import new_session; session = new_session('u2net'); print('Model downloaded successfully')" \
    # Clean up pip cache and temporary files
    && pip cache purge \
    && rm -rf /root/.cache/pip \
    && rm -rf /tmp/* \
    && find /usr/local/lib/python3.11 -name "*.pyc" -delete \
    && find /usr/local/lib/python3.11 -name "__pycache__" -type d -exec rm -rf {} + 2>/dev/null || true

# Copy application code
COPY app.py .

# Create necessary directories and set permissions
RUN mkdir -p /tmp/uploads \
    && chown -R app:app /app \
    && chown -R app:app /tmp/uploads

# Switch to non-root user for security
USER app

# Expose the port
EXPOSE 8080

# Add health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=30s --retries=3 \
    CMD python -c "import requests; requests.get('http://localhost:8080/health', timeout=5)" || exit 1

# Production command using gunicorn
CMD ["gunicorn", \
     "--bind", "0.0.0.0:8080", \
     "--workers", "1", \
     "--threads", "4", \
     "--worker-class", "gthread", \
     "--worker-connections", "1000", \
     "--max-requests", "1000", \
     "--max-requests-jitter", "100", \
     "--timeout", "120", \
     "--keep-alive", "2", \
     "--log-level", "warning", \
     "--access-logfile", "-", \
     "--error-logfile", "-", \
     "app:app"]