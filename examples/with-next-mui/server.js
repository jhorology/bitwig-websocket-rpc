const { createServer } = require('http'),
      { parse } = require('url'),
      next = require('next'),
      dev = process.env.NODE_ENV !== 'production',
      app = next({ dev }),
      handle = app.getRequestHandler(),
      PORT = 3000


app.prepare().then(() => {
  createServer((req, res) => {
    // Be sure to pass `true` as the second argument to `url.parse`.
    // This tells it to parse the query portion of the URL.
    const parsedUrl = parse(req.url, true)
    // const { pathname, query } = parsedUrl

    // if (pathname === '/a') {
    //   app.render(req, res, '/b', query)
    // } else if (pathname === '/b') {
    //   app.render(req, res, '/a', query)
    // } else {
    //   handle(req, res, parsedUrl)
    // }
    handle(req, res, parsedUrl)
  }).listen(PORT, err => {
    if (err) throw err
    console.info(`> Ready on http://localhost:${PORT}`)
  })
})
