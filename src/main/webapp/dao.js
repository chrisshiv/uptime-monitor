class Dao {

  constructor() {
    console.log('Dao initialized');
  }

  loadSites() {
    return $.get('/api/site');    
  }

  getSite(site) {
    return $.get('/api/site/' + site);    
  }

  loadSiteLog(site, pageNumber = 0, zoom = 1) {
    return $.get('/api/log/' + site + '?size=120&page=' + pageNumber + '&zoom=' + zoom)
      .then(data => {
        data.avg = Math.round(data.content.reduce((prev, current) => prev + current.responseTime, 0) / data.content.length);
        return data;
      });
  }

}

const dao = new Dao();