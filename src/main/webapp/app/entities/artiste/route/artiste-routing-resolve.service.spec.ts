jest.mock('@angular/router');

import { TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of } from 'rxjs';

import { IArtiste, Artiste } from '../artiste.model';
import { ArtisteService } from '../service/artiste.service';

import { ArtisteRoutingResolveService } from './artiste-routing-resolve.service';

describe('Service Tests', () => {
  describe('Artiste routing resolve service', () => {
    let mockRouter: Router;
    let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
    let routingResolveService: ArtisteRoutingResolveService;
    let service: ArtisteService;
    let resultArtiste: IArtiste | undefined;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        providers: [Router, ActivatedRouteSnapshot],
      });
      mockRouter = TestBed.inject(Router);
      mockActivatedRouteSnapshot = TestBed.inject(ActivatedRouteSnapshot);
      routingResolveService = TestBed.inject(ArtisteRoutingResolveService);
      service = TestBed.inject(ArtisteService);
      resultArtiste = undefined;
    });

    describe('resolve', () => {
      it('should return existing IArtiste for existing id', () => {
        // GIVEN
        service.find = jest.fn(id => of(new HttpResponse({ body: new Artiste(id) })));
        mockActivatedRouteSnapshot.params = { id: 123 };

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultArtiste = result;
        });

        // THEN
        expect(service.find).toBeCalledWith(123);
        expect(resultArtiste).toEqual(new Artiste(123));
      });

      it('should return new IArtiste if id is not provided', () => {
        // GIVEN
        service.find = jest.fn();
        mockActivatedRouteSnapshot.params = {};

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultArtiste = result;
        });

        // THEN
        expect(service.find).not.toBeCalled();
        expect(resultArtiste).toEqual(new Artiste());
      });

      it('should route to 404 page if data not found in server', () => {
        // GIVEN
        spyOn(service, 'find').and.returnValue(of(new HttpResponse({ body: null })));
        mockActivatedRouteSnapshot.params = { id: 123 };

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultArtiste = result;
        });

        // THEN
        expect(service.find).toBeCalledWith(123);
        expect(resultArtiste).toEqual(undefined);
        expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
      });
    });
  });
});
