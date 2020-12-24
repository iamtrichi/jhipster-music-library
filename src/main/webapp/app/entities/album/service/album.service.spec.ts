import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import * as dayjs from 'dayjs';

import { DATE_FORMAT, } from 'app/config/input.constants';
import { IAlbum, Album } from '../album.model';

import { AlbumService } from './album.service';

describe('Service Tests', () => {
    describe('Album Service', () => {
        let service: AlbumService;
        let httpMock: HttpTestingController;
        let elemDefault: IAlbum;
        let expectedResult: IAlbum | IAlbum[] | boolean | null;
        let currentDate: dayjs.Dayjs;

        beforeEach(() => {
            TestBed.configureTestingModule({
                imports: [
                    HttpClientTestingModule
                ]
            });
            expectedResult = null;
            service = TestBed.inject(AlbumService);
            httpMock = TestBed.inject(HttpTestingController);
            currentDate = dayjs();

            elemDefault = new Album(
                0,
                'AAAAAAA',
                currentDate,
            );
        });

        describe('Service methods', () => {
            it('should find an element', () => {
                const returnedFromService = Object.assign({
                    releaseDate: currentDate.format(DATE_FORMAT),
                }, elemDefault);

                service.find(123).subscribe(resp => expectedResult = resp.body);

                const req = httpMock.expectOne({ method: 'GET' });
                req.flush(returnedFromService);
                expect(expectedResult).toMatchObject(elemDefault);
            });

            it('should create a Album', () => {
                const returnedFromService = Object.assign({
                    id: 0,
                    releaseDate: currentDate.format(DATE_FORMAT),
                }, elemDefault);

                const expected = Object.assign({
                    releaseDate: currentDate,
                }, returnedFromService);

                service.create(new Album()).subscribe(resp => expectedResult = resp.body);

                const req = httpMock.expectOne({ method: 'POST' });
                req.flush(returnedFromService);
                expect(expectedResult).toMatchObject(expected);
            });

            it('should update a Album', () => {
                const returnedFromService = Object.assign({
                    id: 1,
                    title: 'BBBBBB',
                    releaseDate: currentDate.format(DATE_FORMAT),
                }, elemDefault);

                const expected = Object.assign({
                    releaseDate: currentDate,
                }, returnedFromService);

                service.update(expected).subscribe(resp => expectedResult = resp.body);

                const req = httpMock.expectOne({ method: 'PUT' });
                req.flush(returnedFromService);
                expect(expectedResult).toMatchObject(expected);
            });

            it('should return a list of Album', () => {
                const returnedFromService = Object.assign({
                    id: 1,
                    title: 'BBBBBB',
                    releaseDate: currentDate.format(DATE_FORMAT),
                }, elemDefault);

                const expected = Object.assign({
                    releaseDate: currentDate,
                }, returnedFromService);

                service.query().subscribe(resp => expectedResult = resp.body);

                const req = httpMock.expectOne({ method: 'GET' });
                req.flush([returnedFromService]);
                httpMock.verify();
                expect(expectedResult).toContainEqual(expected);
            });

            it('should delete a Album', () => {
                service.delete(123).subscribe(resp => expectedResult = resp.ok);

                const req = httpMock.expectOne({ method: 'DELETE' });
                req.flush({ status: 200 });
                expect(expectedResult);
            });
        });

        afterEach(() => {
            httpMock.verify();
        });
    });
});
