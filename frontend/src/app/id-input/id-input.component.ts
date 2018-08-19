import { Component, OnInit } from '@angular/core';
import { faArrowRight } from '@fortawesome/free-solid-svg-icons';
import { Router } from '@angular/router';

@Component({
  selector: 'app-id-input',
  templateUrl: './id-input.component.html',
  styleUrls: ['./id-input.component.scss'],
})
export class IdInputComponent implements OnInit {
  personId: string = localStorage.getItem('cocktails.identity.id') || '';
  personName: string = localStorage.getItem('cocktails.identity.name') || '';
  faArrowRight = faArrowRight;

  constructor(private router: Router) {}

  ngOnInit() {}

  savePersonData() {
    localStorage.setItem('cocktails.identity.id', this.personId);
    localStorage.setItem('cocktails.identity.name', this.personName);
    this.router.navigate(['']);
  }

  idTooShort() {
    return `${this.personId}`.length < 2;
  }
}
