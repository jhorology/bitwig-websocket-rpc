module.exports = {
  serverRuntimeConfig: {
    // Will only be available on the server side
  },
  publicRuntimeConfig: {
    // Will be available on both server and client

    // use SSDP UPnP service, custom-server is needed
    useSSDP: process.env.USE_SSDP
  }
}
