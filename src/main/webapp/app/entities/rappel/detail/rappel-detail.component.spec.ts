import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { RappelDetailComponent } from './rappel-detail.component';

describe('Rappel Management Detail Component', () => {
  let comp: RappelDetailComponent;
  let fixture: ComponentFixture<RappelDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [RappelDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ rappel: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(RappelDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(RappelDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load rappel on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.rappel).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
