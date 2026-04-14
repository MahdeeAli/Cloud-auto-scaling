# Use a lightweight Python image
FROM python:3.9-slim

# Set the working directory
WORKDIR /app

# Install ALL system build tools required to compile gevent from source
# The 'rm -rf' command at the end clears the download cache to keep your image size tiny!
RUN apt-get update && \
    apt-get install -y gcc python3-dev make file && \
    rm -rf /var/lib/apt/lists/*

# Copy requirements and install them
COPY requirements.txt .
RUN pip install --upgrade pip && \
    pip install --no-cache-dir -r requirements.txt

# Copy the app code
COPY app.py .

# Run the app
CMD ["python", "app.py"]