export DOCKER_DEFAULT_PLATFORM=linux/amd64

docker buildx build . --build-arg PLATFORM=cashier -t jeepay-ui-cashier:latest

docker buildx build . --build-arg PLATFORM=manager -t jeepay-ui-manager:latest

docker buildx build . --build-arg PLATFORM=merchant -t jeepay-ui-merchant:latest
