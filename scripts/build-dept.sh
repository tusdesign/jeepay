export DOCKER_DEFAULT_PLATFORM=linux/amd64

docker build -t oci.tuxm.art:8443/tusdesign/jeepay-deps:latest -f docs/Dockerfile .
docker push oci.tuxm.art:8443/tusdesign/jeepay-deps:latest
