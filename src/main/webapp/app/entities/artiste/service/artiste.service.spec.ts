import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import * as dayjs from 'dayjs';

import { DATE_FORMAT, } from 'app/config/input.constants';
import { IArtiste, Artiste } from '../artiste.model';

import { ArtisteService } from './artiste.service';

describe('Service Tests', () => {
    describe('Artiste Service', () => {
        let service: ArtisteService;
        let httpMock: HttpTestingController;
        let elemDefault: IArtiste;
        let expectedResult: IArtiste | IArtiste[] | boolean | null;
        let currentDate: dayjs.Dayjs;

        beforeEach(() => {
            TestBed.configureTestingModule({
                imports: [
                    HttpClientTestingModule
                ]
            });
            expectedResult = null;
            service = TestBed.inject(ArtisteService);
            httpMock = TestBed.inject(HttpTestingController);
            currentDate = dayjs();

            elemDefault = new Artiste(
                0,
                'AAAAAAA',
                'AAAAAAA',
                currentDate,
            );
        });

        describe('Service methods', () => {
            it('should find an element', () => {
                const returnedFromService = Object.assign({
                    dateNaiss: currentDate.format(DATE_FORMAT),
                }, elemDefault);

                service.find(123).subscribe(resp => expectedResult = resp.body);

                const req = httpMock.expectOne({ method: 'GET' });
                req.flush(returnedFromService);
                expect(expectedResult).toMatchObject(elemDefault);
            });

            it('should create a Artiste', () => {
                const returnedFromService = Object.assign({
                    id: 0,
                    dateNaiss: currentDate.format(DATE_FORMAT),
                }, elemDefault);

                const expected = Object.assign({
                    dateNaiss: currentDate,
                }, returnedFromService);

                service.create(new Artiste()).subscribe(resp => expectedResult = resp.body);

                const req = httpMock.expectOne({ method: 'POST' });
                req.flush(returnedFromService);
                expect(expectedResult).toMatchObject(expected);
            });

            it('should update a Artiste', () => {
                const returnedFromService = Object.assign({
                    id: 1,
                    firstName: 'BBBBBB',
                    lastName: 'BBBBBB',
                    dateNaiss: currentDate.format(DATE_FORMAT),
                }, elemDefault);

                const expected = Object.assign({
                    dateNaiss: currentDate,
                }, returnedFromService);

                service.update(expected).subscribe(resp => expectedResult = resp.body);

                const req = httpMock.expectOne({ method: 'PUT' });
                req.flush(returnedFromService);
                expect(expectedResult).toMatchObject(expected);
            });

            it('should return a list of Artiste', () => {
                const returnedFromService = Object.assign({
                    id: 1,
                    firstName: 'BBBBBB',
                    lastName: 'BBBBBB',
                    dateNaiss: currentDate.format(DATE_FORMAT),
                }, elemDefault);

                const expected = Object.assign({
                    dateNaiss: currentDate,
                }, returnedFromService);

                service.query().subscribe(resp => expectedResult = resp.body);

                const req = httpMock.expectOne({ method: 'GET' });
                req.flush([returnedFromService]);
                httpMock.verify();
                expect(expectedResult).toContainEqual(expected);
            });

            it('should delete a Artiste', () => {
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
