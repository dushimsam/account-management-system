FROM node:14-alpine

# Set working directory
WORKDIR /app

# Remove .next folder
RUN rm -rf .next

# Clean cache and delete node_modules
RUN npm cache clean --force && \
    rm -rf node_modules

# Copy package.json and package-lock.json
COPY package*.json ./

# Install the required npm version
RUN npm install -g npm@6.14.18

# # Remove the package-lock.json file
# RUN rm -f package-lock.json

# Install dependencies
RUN npm install

# Copy the rest of the application code
COPY . .

RUN npm run build

EXPOSE 3000

# Start the application
CMD ["npm", "start"]