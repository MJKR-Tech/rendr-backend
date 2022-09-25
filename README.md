# rendr
Customizable Report Rendering with MJKR-Tech’s Rendr for GSEngage21, built with Docker.

Step 1:
Download docker and sign up at docker.com

Step 2: Run the following through any CLI
docker pull michaeleelele/rendr-backend
docker pull michaeleelele/mysql:5.7

Step 3:
Run docker image and backend should work. An example below would suffice
docker run --network michaeleelele/rendr-backend --name rendr-backend-container -p 8080:8080 -d michaeleelele/rendr-backend

