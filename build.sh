#!/bin/sh

./gradlew clean build
cd account && docker build -t simple2pc/account .
cd ../offer && docker build -t simple2pc/offer .
cd ../kong && docker build -t simple2pc/kong .