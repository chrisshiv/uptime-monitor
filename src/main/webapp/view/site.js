const ViewSite = {
  template: '#view-site',

  data: function() {
    return {
      pageNumber: 0,
      zoom: window.innerWidth <= 800 ? 0 : 5,
      site: null,
      siteInfo: {},
      logs: null,
      chart: null,
      touchStart: null
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

  mounted() {
    $('#chart').bind('DOMMouseScroll mousewheel', (e) => {
      if (e.originalEvent.wheelDelta > 0 || e.originalEvent.detail < 0) {
        this.zoomIn();
      } else {
        this.zoomOut();        
      }
    });
    $('#chart').bind('touchstart', (e) => {
      this.touchStart = e.originalEvent.touches[0] || e.originalEvent.changedTouches[0];
    });
    $('#chart').bind('touchend', (e) => {
      if (this.touchStart != null) {
        const touch = e.originalEvent.touches[0] || e.originalEvent.changedTouches[0];
        const offsetX = touch.pageX - this.touchStart.pageX;
        const offsetY = touch.pageY - this.touchStart.pageY;
        if (Math.abs(offsetX / offsetY) > 2 && Math.abs(offsetX) > 50) {
          offsetX > 0 ? this.pageOlder() : this.pageNewer();
        }
        this.touchesStart = null;
      }
    });
  },

  watch: {
    // call again the method if the route changes
    '$route': 'newRoute'
  },

  methods: {
    newRoute() {
      this.pageNumber = 0;
      this.zoom = window.innerWidth <= 800 ? 0 : 5;
      this.refresh(true);
    },
    refresh(loading) {
      this.site = this.$route.params.site;
      if (loading) $bus.$emit('loading', true);
      if (this.site) {
        dao.getSite(this.site).then(data => this.siteInfo = data);
        dao.loadSiteLog(this.site, this.pageNumber, this.zoom).then(data => {
          this.logs = data;
          const chartData = [...data.content].reverse();
          if (this.chart == null) {
            this.chart = initChart();
          }
          this.chart.data.srcData = chartData;
          var lastDatePart = chartData[0].date.substring(0, 10);
          this.chart.data.labels = chartData.map((log, i) => {
            const time = log.date.substring(11, 16);
            const datePart = log.date.substring(0, 10);
            if (datePart != lastDatePart) {
              lastDatePart = datePart;
              return datePart + ' ' + time;
            } else {
              return time;
            }
          });
          this.chart.data.datasets[0].data = chartData.map(log => log.responseTime);
          this.chart.update();
          if (loading) $bus.$emit('loading', false);
        });
      }
    },
    pageOlder() {
      if (!this.logs.last) {
        this.pageNumber++;
        this.refresh(true);
      }
    },
    pageNewer() {
      this.pageNumber = Math.max(this.pageNumber - 1, 0);
      this.refresh(true);
    },
    zoomOut() {
      this.zoom = Math.min(this.zoom + 1, 10);
      this.pageNumber = 0;
      this.refresh(true);
    },
    zoomIn() {
      if (this.zoom - 1 > 0) {
        this.zoom--;
        this.pageNumber = 0;
        this.refresh(true);
      }
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