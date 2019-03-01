const Path = require('path')
const express = require('express')
// const logger = require('morgan')

var app = express()
var port = process.env.PORT || 8080

// app.use(logger('dev'))

app.use(express.static(Path.join(__dirname, '/dist')))

app.get('*', function (request, response) {
  response.sendfile(Path.join(__dirname, '/dist/index.html'))
})

app.listen(port, () => {
  console.log('server started on port ' + port)
})
