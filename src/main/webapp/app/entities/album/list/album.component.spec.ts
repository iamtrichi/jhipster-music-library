jest.mock('@angular/router');

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { of } from 'rxjs';

import { AlbumService } from '../service/album.service';
import { Album } from '../album.model';

import { AlbumComponent } from './album.component';

describe('Component Tests', () => {
    describe('Album Management Component', () => {
        let comp: AlbumComponent;
        let fixture: ComponentFixture<AlbumComponent>;
        let service: AlbumService;

        beforeEach(() => {
            TestBed.configureTestingModule({
                imports: [HttpClientTestingModule],
                declarations: [AlbumComponent],
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
            .overrideTemplate(AlbumComponent, '')
            .compileComponents();

            fixture = TestBed.createComponent(AlbumComponent);
            comp = fixture.componentInstance;
            service = TestBed.inject(AlbumService);
        });

        it('Should call load all on init', () => {
            // GIVEN
            const headers = new HttpHeaders().append('link', 'link;link');
            spyOn(service, 'query').and.returnValue(of(new HttpResponse({
                body: [new Album(123)],
                headers
            })));

            // WHEN
            comp.ngOnInit();

            // THEN
            expect(service.query).toHaveBeenCalled();
            expect(comp.albums?.[0]).toEqual(jasmine.objectContaining({ id: 123 }));
        });

        it('should load a page', () => {
            // GIVEN
            const headers = new HttpHeaders().append('link', 'link;link');
            spyOn(service, 'query').and.returnValue(
                of(
                    new HttpResponse({
                        body: [new Album(123)],
                        headers
                    })
                )
            );

            // WHEN
            comp.loadPage(1);

            // THEN
            expect(service.query).toHaveBeenCalled();
            expect(comp.albums?.[0]).toEqual(jasmine.objectContaining({ id: 123 }));
        });


        it('should calculate the sort attribute for an id', () => {
            // WHEN
            comp.ngOnInit();
            const result = comp.sort();

            // THEN
            expect(result).toEqual(['id,desc']);
        });

        it('should calculate the sort attribute for a non-id attribute', () => {
            // INIT
            comp.ngOnInit();

            // GIVEN
            comp.predicate = 'name';

            // WHEN
            const result = comp.sort();

            // THEN
            expect(result).toEqual(['name,desc', 'id']);
        });

    });
});
