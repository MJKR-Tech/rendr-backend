# rendr
Customizable Report Rendering with MJKR-Tech’s Rendr for GSEngage21, built with Docker.

Step 1:
Download docker and sign up [here](https://www.docker.com)

Step 2: Run the following through any CLI
  - `docker pull michaeleelele/rendr:mysql`
  - `docker pull michaeleelele/rendr:rendr-backend`

Step 3:
Run docker image for mysql through the following commands-

  - `docker run --name mysqldb --network rendr-backend -e MYSQL_ROOT_PASSWORD=<MySQL password> -e MYSQL_DATABASE=rendr_db -e MYSQL_USER=rendrUser -e MYSQL_PASSWORD=rendrMJKRTech -d mysql`

  - `docker exec -it <image ID> bash`

  - `mysql -urendrUser -prendrMJKRTech`


Step 4:
Follow README [here](src/main/resources/mysql)  

  
Step 5:
Run docker image for rendr-backend. An example below would suffice

  - `docker run --network michaeleelele/rendr-backend --name rendr-backend-container -p 8081:8081 -d michaeleelele/rendr-backend`

