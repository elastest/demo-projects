FROM node:9.11.1-alpine

COPY sendPost /sendPost

RUN npm install -g @angular/cli
RUN cd /sendPost && \
    rm -rf node_modules && \
    npm install

RUN cd /sendPost &&\
    ng build --env=prod;

RUN cd /sendPost && \
    npm install -g http-server

EXPOSE 8080

CMD cd /sendPost/dist; exec http-server;

