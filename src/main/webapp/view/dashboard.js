const ViewDashboard = {
  template: '#view-dashboard',

  data: function() {
    return {
      sites: null
    }
  },

  created () {
    console.log('Init ViewDashboard');

    $bus.$emit('loading', true);
    dao.loadSites().then(data => {
      this.sites = data;
      $bus.$emit('loading', false)
    });
  }

}