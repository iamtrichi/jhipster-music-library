jest.mock('@angular/router');

import { TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of } from 'rxjs';

import { ISong, Song } from '../song.model';
import { SongService } from '../service/song.service';

import { SongRoutingResolveService } from './song-routing-resolve.service';

describe('Service Tests', () => {
  describe('Song routing resolve service', () => {
    let mockRouter: Router;
    let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
    let routingResolveService: SongRoutingResolveService;
    let service: SongService;
    let resultSong: ISong | undefined;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        providers: [Router, ActivatedRouteSnapshot],
      });
      mockRouter = TestBed.inject(Router);
      mockActivatedRouteSnapshot = TestBed.inject(ActivatedRouteSnapshot);
      routingResolveService = TestBed.inject(SongRoutingResolveService);
      service = TestBed.inject(SongService);
      resultSong = undefined;
    });

    describe('resolve', () => {
      it('should return existing ISong for existing id', () => {
        // GIVEN
        service.find = jest.fn(id => of(new HttpResponse({ body: new Song(id) })));
        mockActivatedRouteSnapshot.params = { id: 123 };

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultSong = result;
        });

        // THEN
        expect(service.find).toBeCalledWith(123);
        expect(resultSong).toEqual(new Song(123));
      });

      it('should return new ISong if id is not provided', () => {
        // GIVEN
        service.find = jest.fn();
        mockActivatedRouteSnapshot.params = {};

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultSong = result;
        });

        // THEN
        expect(service.find).not.toBeCalled();
        expect(resultSong).toEqual(new Song());
      });

      it('should route to 404 page if data not found in server', () => {
        // GIVEN
        spyOn(service, 'find').and.returnValue(of(new HttpResponse({ body: null })));
        mockActivatedRouteSnapshot.params = { id: 123 };

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultSong = result;
        });

        // THEN
        expect(service.find).toBeCalledWith(123);
        expect(resultSong).toEqual(undefined);
        expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
      });
    });
  });
});
