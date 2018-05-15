class Dao {

  constructor() {
    console.log('Dao initialized');
  }

  loadSites() {
    return $.get('/api/site');    
  }

  loadSiteLog(site) {
    return $.get('/api/log/' + site);
  }

}

const dao = new Dao();