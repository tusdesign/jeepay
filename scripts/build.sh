export DOCKER_DEFAULT_PLATFORM=linux/amd64

docker buildx build . --build-arg PORT=9216 --build-arg PLATFORM=payment -t oci.tuxm.art:8443/tusdesign/jeepay-payment:latest
docker push oci.tuxm.art:8443/tusdesign/jeepay-payment:latest
docker buildx build . --build-arg PORT=9217 --build-arg PLATFORM=manager -t oci.tuxm.art:8443/tusdesign/jeepay-manager:latest
docker push oci.tuxm.art:8443/tusdesign/jeepay-manager:latest
docker buildx build . --build-arg PORT=9218 --build-arg PLATFORM=merchant -t oci.tuxm.art:8443/tusdesign/jeepay-merchant:latest
docker push oci.tuxm.art:8443/tusdesign/jeepay-merchant:latest

