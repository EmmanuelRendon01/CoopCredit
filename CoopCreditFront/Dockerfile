# Dockerfile for CoopCredit Frontend
# Uses nginx:alpine for a lightweight static file server

FROM nginx:alpine

# Remove default nginx website
RUN rm -rf /usr/share/nginx/html/*

# Copy frontend files
COPY . /usr/share/nginx/html

# Copy custom nginx configuration
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Expose port 3001
EXPOSE 3001

# Start nginx
CMD ["nginx", "-g", "daemon off;"]
