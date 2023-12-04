# Use an official Python image as a base image
FROM python:3.8

# Set the working directory in the container
WORKDIR /app

# Install Java
RUN apt-get update && apt-get install -y default-jre default-jdk

# Set the JAVA_HOME environment variable
ENV JAVA_HOME /usr/lib/jvm/default-java
ENV PATH $PATH:$JAVA_HOME/bin

# Copy the current directory contents into the container at /app
COPY . .

# Install any needed packages specified in requirements.txt
RUN pip install --no-cache-dir -r requirements.txt

RUN chmod -R 777 /app

# Make port 4000 available to the world outside this container
EXPOSE 4000

# Run app.py when the container launches
CMD ["uvicorn", "app:app", "--host", "0.0.0.0", "--port", "4000", "--workers", "4"]
