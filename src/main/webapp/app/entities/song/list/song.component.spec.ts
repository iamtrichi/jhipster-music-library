jest.mock('@angular/router');

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { of } from 'rxjs';

import { SongService } from '../service/song.service';
import { Song } from '../song.model';

import { SongComponent } from './song.component';

describe('Component Tests', () => {
    describe('Song Management Component', () => {
        let comp: SongComponent;
        let fixture: ComponentFixture<SongComponent>;
        let service: SongService;

        beforeEach(() => {
            TestBed.configureTestingModule({
                imports: [HttpClientTestingModule],
                declarations: [SongComponent],
                providers: [
                    Router,
                    {
                        provide: ActivatedRoute,
                        useValue: {
                            data: of({
                                defaultSort: 'id,asc',
                            }),
                            queryParamMap: of(
                                jest.requireActual('@angular/router').convertToParamMap({
                                    page: '1',
                                    size: '1',
                                    sort: 'id,desc',
                                })
                            ),
                        }
                    }
                ]
            })
            .overrideTemplate(SongComponent, '')
            .compileComponents();

            fixture = TestBed.createComponent(SongComponent);
            comp = fixture.componentInstance;
            service = TestBed.inject(SongService);
        });

        it('Should call load all on init', () => {
            // GIVEN
            const headers = new HttpHeaders().append('link', 'link;link');
            spyOn(service, 'query').and.returnValue(of(new HttpResponse({
                body: [new Song(123)],
                headers
            })));

            // WHEN
            comp.ngOnInit();

            // THEN
            expect(service.query).toHaveBeenCalled();
            expect(comp.songs[0]).toEqual(jasmine.objectContaining({ id: 123 }));
        });

        it('should load a page', () => {
            // GIVEN
            const headers = new HttpHeaders().append('link', 'link;link');
            spyOn(service, 'query').and.returnValue(
                of(
                    new HttpResponse({
                        body: [new Song(123)],
                        headers
                    })
                )
            );

            // WHEN
            comp.loadPage(1);

            // THEN
            expect(service.query).toHaveBeenCalled();
            expect(comp.songs[0]).toEqual(jasmine.objectContaining({ id: 123 }));
        });


        it('should re-initialize the page', () => {
            // GIVEN
            const headers = new HttpHeaders().append('link', 'link;link');
            spyOn(service, 'query').and.returnValue(
                of(
                    new HttpResponse({
                        body: [new Song(123)],
                        headers
                    })
                )
            );

            // WHEN
            comp.loadPage(1);
            comp.reset();

            // THEN
            expect(comp.page).toEqual(0);
            expect(service.query).toHaveBeenCalledTimes(2);
            expect(comp.songs[0]).toEqual(jasmine.objectContaining({ id: 123 }));
        });

        it('should calculate the sort attribute for an id', () => {
            // WHEN
            comp.ngOnInit();
            const result = comp.sort();

            // THEN
            expect(result).toEqual(['id,asc']);
        });

        it('should calculate the sort attribute for a non-id attribute', () => {
            // INIT
            comp.ngOnInit();

            // GIVEN
            comp.predicate = 'name';

            // WHEN
            const result = comp.sort();

            // THEN
                expect(result).toEqual(['name,asc', 'id']);
        });

    });
});
