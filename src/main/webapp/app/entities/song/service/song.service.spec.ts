import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import * as dayjs from 'dayjs';

import { DATE_FORMAT, } from 'app/config/input.constants';
import { ISong, Song } from '../song.model';

import { SongService } from './song.service';

describe('Service Tests', () => {
    describe('Song Service', () => {
        let service: SongService;
        let httpMock: HttpTestingController;
        let elemDefault: ISong;
        let expectedResult: ISong | ISong[] | boolean | null;
        let currentDate: dayjs.Dayjs;

        beforeEach(() => {
            TestBed.configureTestingModule({
                imports: [
                    HttpClientTestingModule
                ]
            });
            expectedResult = null;
            service = TestBed.inject(SongService);
            httpMock = TestBed.inject(HttpTestingController);
            currentDate = dayjs();

            elemDefault = new Song(
                0,
                'AAAAAAA',
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

            it('should create a Song', () => {
                const returnedFromService = Object.assign({
                    id: 0,
                    releaseDate: currentDate.format(DATE_FORMAT),
                }, elemDefault);

                const expected = Object.assign({
                    releaseDate: currentDate,
                }, returnedFromService);

                service.create(new Song()).subscribe(resp => expectedResult = resp.body);

                const req = httpMock.expectOne({ method: 'POST' });
                req.flush(returnedFromService);
                expect(expectedResult).toMatchObject(expected);
            });

            it('should update a Song', () => {
                const returnedFromService = Object.assign({
                    id: 1,
                    name: 'BBBBBB',
                    url: 'BBBBBB',
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

            it('should return a list of Song', () => {
                const returnedFromService = Object.assign({
                    id: 1,
                    name: 'BBBBBB',
                    url: 'BBBBBB',
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

            it('should delete a Song', () => {
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
