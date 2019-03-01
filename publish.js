const ghpages = require('gh-pages')

ghpages.publish('dist', (err) => {
  if (err !== undefined) {
    console.log('err: ' + err)
  }
  else {
    console.log('published')
  }
})
