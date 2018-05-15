const ViewSite = {
  template: '#view-site',

  data: function() {
    return {
      site: null,
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
    '$route': 'refresh'
  },

  methods: {
    refresh(loading) {
      this.site = this.$route.params.site;
      if (loading) $bus.$emit('loading', true);
      dao.loadSiteLog(this.site).then(data => {
        this.logs = data;
        const chartData = [...data].reverse();
        if (this.chart == null) {
          this.chart = initChart();
        }
        this.chart.data.srcData = chartData;
        this.chart.data.labels = chartData.map(log => log.date.substring(11, 16));
        this.chart.data.datasets[0].data = chartData.map(log => log.responseTime);
        this.chart.update();
        if (loading) $bus.$emit('loading', false);
      });
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