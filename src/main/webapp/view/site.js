const ViewSite = {
  template: '#view-site',

  data: function() {
    return {
      pageNumber: 0,
      zoom: 1,
      site: null,
      siteInfo: {},
      logs: null,
      chart: null
    }
  },

  created () {
    console.log('Init ViewSite');

    var self = this;
    setInterval(() => {
      self.refresh();
    }, 60 * 1000);
    this.refresh(true);
  },

  watch: {
    // call again the method if the route changes
    '$route': 'newRoute'
  },

  methods: {
    newRoute() {
      this.pageNumber = 0;
      this.refresh(true);
    },
    refresh(loading) {
      this.site = this.$route.params.site;
      if (loading) $bus.$emit('loading', true);
      dao.getSite(this.site).then(data => this.siteInfo = data);
      dao.loadSiteLog(this.site, this.pageNumber, this.zoom).then(data => {
        this.logs = data;
        const chartData = [...data.content].reverse();
        if (this.chart == null) {
          this.chart = initChart();
        }
        this.chart.data.srcData = chartData;
        this.chart.data.labels = chartData.map(log => log.date.substring(11, 16));
        this.chart.data.datasets[0].data = chartData.map(log => log.responseTime);
        this.chart.update();
        if (loading) $bus.$emit('loading', false);
      });
    },
    pageOlder() {
      this.pageNumber++;
      this.refresh(true);
    },
    pageNewer() {
      this.pageNumber = Math.max(this.pageNumber - 1, 0);
      this.refresh(true);
    },
    zoomOut() {
      this.zoom = Math.min(this.zoom + 1, 10);
      this.refresh(true);
    },
    zoomIn() {
      this.zoom = Math.max(this.zoom - 1, 1);
      this.refresh(true);
    }
  }
}

function initChart() {
  return new Chart($('#chart'), {
    type: 'line',
    data: {
      labels: [],
      datasets: [{
        backgroundColor: 'rgba(255,99,132,1)',
        borderColor: 'rgba(255,99,132,1)',
        data: [],
        fill: false
      }]
    },
    options: {
      responsive: true,
      legend: {
        display: false
      },
      tooltips: {
        mode: 'nearest',
        intersect: false,
        callbacks: {
          title: function(tooltipItems, data) {
            return data.srcData[tooltipItems[0].index].date.replace('@', ' ');
          },
          label: function(tooltipItem, data) {
            return 'Response time: ' + data.srcData[tooltipItem.index].responseTime;
          },
          footer: function(tooltipItems, data) {
            return 'Status: ' + data.srcData[tooltipItems[0].index].status + ' (content length: ' + data.srcData[tooltipItems[0].index].contentLength + ')';
          },
        },
      },
      hover: {
        mode: 'nearest',
        intersect: true
      },
      scales: {
        xAxes: [{
          display: true,
          scaleLabel: {
            display: false,
          }
        }],
        yAxes: [{
          display: true,
          scaleLabel: {
            display: true,
            labelString: 'Response time'
          }
        }]
      }
    }
  });
}