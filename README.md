docker build -t mys .

docker network create mysql
docker run --network mysql -e MYSQL_USER=user -e MYSQL_PASSWORD=password -e MYSQL_DATABASE=database -e MYSQL_ALLOW_EMPTY_PASSWORD=1 -p 3306:3306 --name mysql --rm -d cgr.dev/chainguard-private/mysql

docker run --network mysql -e JDBC=jdbc:mysql://mysql:3306/database -e DB_USER=user -e DB_PASSWORD=password mys

Successfully connected to the database!
Query result: 1

