#!/bin/sh

./gradle build
cd account && docker build -t simple2pc/account .
