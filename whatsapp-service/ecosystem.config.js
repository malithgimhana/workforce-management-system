module.exports = {
  apps: [{
    name: 'flexiwork-whatsapp',
    script: 'server.js',
    watch: false,
    restart_delay: 3000,
    max_restarts: 10,
    min_uptime: '5s',
    log_date_format: 'YYYY-MM-DD HH:mm:ss',
    error_file: './logs/error.log',
    out_file: './logs/out.log',
    env: {
      NODE_ENV: 'production',
      PORT: 3001
    }
  }]
};
