#! /bin/bash

errors=0

echo -e "\n\e[36m - docker version\e[0m"
if ! [ -x "$(command -v docker)" ];
then
    echo -e "\e[31m   Need to install docker\e[0m\n"
    errors=1
else
    docker version
fi

# Pip (required for Docker Compose)
echo -e "\n\e[36m - pip version\e[0m"
if ! [ -x "$(command -v pip)" ];
then
    echo -e "\e[31m   Need to install pip\e[0m\n"
    errors=1
else
    pip --version
fi

# Docker Compose
echo -e "\n\e[36m - docker-compose version\e[0m"
if ! [ -x "$(command -v docker-compose)" ];
then
    echo -e "\e[31m   Need to install docker for running tests\e[0m\n"
    errors=1
else
    docker-compose version
fi


exit $errors
