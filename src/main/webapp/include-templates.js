var includeTemplates = {
  counter: 0,
  callback: undefined,
  then: function(callback) {
    this.callback = callback;
    return includeTemplates;
  },
  process: function() {
    /*loop through a collection of all HTML elements:*/
    var z = document.getElementsByTagName('script');
    for (var i = 0; i < z.length; i++) {
      var elmnt = z[i];
      /*search for elements with a certain atrribute:*/
      var file = elmnt.getAttribute('template-src');
      if (file) {
        includeTemplates.counter++;
        /*make an HTTP request using the attribute value as the file name:*/
        var xhttp = new XMLHttpRequest();
        xhttp.onreadystatechange = function() {
          if (this.readyState == 4) {
            console.log('Loaded template ' + file);
            includeTemplates.counter--;
            if (this.status == 200) {elmnt.innerHTML = this.responseText;}
            if (this.status == 404) {elmnt.innerHTML = "<h4>404 - Page not found, sorry.</h4>";}
            /*remove the attribute, and call this function once more:*/
            elmnt.removeAttribute('template-src');
            includeTemplates.process();
          }
          if (includeTemplates.counter == 0) {
            includeTemplates.callback();
          }
        }
        xhttp.open('GET', file, true);
        xhttp.send();
        return;
      }
    }
  }
}