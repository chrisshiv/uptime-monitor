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
    const size = 120 * zoom;
    return $.get('/api/log/' + site + '?size=' + size + '&page=' + pageNumber)
      .then(data => {
        data.avg = Math.round(data.content.reduce((prev, current) => prev + current.responseTime, 0) / data.content.length);
        data.content = data.content.filter((elem, i) => i % zoom == 0);
        return data;
      });
  }

}

const dao = new Dao();