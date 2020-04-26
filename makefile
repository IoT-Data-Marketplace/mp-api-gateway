build-and-push-to-ecr:
	aws ecr get-login-password --region eu-central-1 --profile mp-ops | docker login --username AWS --password-stdin 543164192837.dkr.ecr.eu-central-1.amazonaws.com/mp-api-gateway
	docker build -t mp-api-gateway .
	docker tag mp-api-gateway:latest 543164192837.dkr.ecr.eu-central-1.amazonaws.com/mp-api-gateway:latest
	docker push 543164192837.dkr.ecr.eu-central-1.amazonaws.com/mp-api-gateway:latest
