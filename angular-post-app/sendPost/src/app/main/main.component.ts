import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-main',
  templateUrl: './main.component.html',
  styleUrls: ['./main.component.css'],
})
export class MainComponent implements OnInit {
  json: any;
  url: string;
  exec: string = 'ti3718';

  constructor(public router: Router) {}

  ngOnInit() {
    this.initParams();
  }

  initParams(): void {
    const params: any = this.router.parseUrl(this.router.url).queryParams;
    if (params.exec) {
      this.exec = params.exec;
    }

    if (params.url) {
      this.url = params.url;
    }

    this.json = {
      '@timestamp': new Date().toISOString(),
      exec: this.exec,
      component: 'fake_component',
      stream: 'webRtc',
      type: 'metric',
      stream_type: 'composed_metrics',
      units: 'unit',
    };
  }

  doPost(): void {
    this.json['@timestamp'] = new Date().toISOString();

    if (this.url) {
      const sendPost = (json) => {
        const http: XMLHttpRequest = new XMLHttpRequest();
        http.open('POST', this.url, true);

        http.setRequestHeader('Content-type', 'application/json');

        http.onreadystatechange = () => {
          // Call a function when the state changes.
          if (http.readyState === 4 && http.status === 200) {
            console.warn('WebRtc stats successfully sent to ' + this.url);
          }
        };
        http.send(json);
      };

      sendPost(JSON.stringify(this.json));
    } else {
      console.error('Error: there is not endpoint url to send post');
    }
  }
}
