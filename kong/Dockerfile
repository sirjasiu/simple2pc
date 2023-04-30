FROM kong
USER root
# Example for GO:
COPY simple2pc /usr/local/kong/plugins/simple2pc
COPY kong.conf /etc/kong/kong.conf
COPY kong.yaml /usr/local/kong/declarative/kong.yml
# reset back the defaults
USER kong
ENTRYPOINT ["/docker-entrypoint.sh"]
EXPOSE 8000 8443 8001 8444
STOPSIGNAL SIGQUIT
HEALTHCHECK --interval=10s --timeout=10s --retries=10 CMD kong health
CMD ["kong", "docker-start"]