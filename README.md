# consumer


Servicio que lee de una cola de activemq


servicio escribe al servicioget por medio de un nginx


-- configuracion de nginx --


```
user  nginx;
worker_processes  1;

error_log  /var/log/nginx/error.log warn;
pid        /var/run/nginx.pid;

events {
    worker_connections  1024;
}

http {
    include       /etc/nginx/mime.types;
    default_type  application/octet-stream;

    log_format  main  '$remote_addr - $remote_user [$time_local] "$request" '
                      '$status $body_bytes_sent "$http_referer" '
                      '"$http_user_agent" "$http_x_forwarded_for"';

    access_log  /var/log/nginx/access.log  main;

    sendfile        on;
    #tcp_nopush     on;

    keepalive_timeout  60;

    #gzip  on;

    upstream app {
    		    server 165.168.1.19:4042;
    		    server 165.168.1.20:4042;
                server 165.168.1.21:4042;
    }

	server {
    		listen 80;
            location / {
                proxy_pass http://app;
                add_header 'X_Upstream' '$upstream_addr';
            }
    }
    # include /etc/nginx/conf.d/*.conf;
}


# stream {

# }
