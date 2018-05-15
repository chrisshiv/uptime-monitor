const ROUTES = [
  { path: '/', component: ViewDashboard },
  { path: '/site/:site', component: ViewSite },
];

var $bus = new Vue();

includeTemplates.then(() => {
  console.log('START VUE');

  Vue.use(VueRouter);

  const router = new VueRouter({
//    mode: 'history',
    routes: ROUTES
  });

  const app = new Vue({
    router,
  
    data: {
      sites: false,
//      currentRoute: window.location.pathname,
      loading: false,
    },
  
    created () {
      this.loading = true;
      $.when(
          dao.loadSites().then(data => this.sites = data)
        ).then(() => this.loading = false);
      $bus.$on('loading', value => this.loading = value);
    },
  
    computed: {
//      ViewComponent () {
//        return ROUTES[this.currentRoute] || { template: '<p>Page not found</p>' };
//      }
    },

    methods: {
    },
//    render (h) { return h(this.ViewComponent) }
  }).$mount('#app');
//  window.addEventListener('popstate', () => {
//    app.currentRoute = window.location.pathname
//  })
}).process();