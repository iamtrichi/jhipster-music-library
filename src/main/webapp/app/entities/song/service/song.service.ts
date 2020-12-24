import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import * as dayjs from 'dayjs';

import { DATE_FORMAT } from 'app/config/input.constants';
import { SERVER_API_URL } from 'app/app.constants';
import { createRequestOption } from 'app/core/request/request-util';
import { ISong } from '../song.model';

type EntityResponseType = HttpResponse<ISong>;
type EntityArrayResponseType = HttpResponse<ISong[]>;

@Injectable({ providedIn: 'root' })
export class SongService {
    public resourceUrl = SERVER_API_URL + 'api/songs';

    constructor(protected http: HttpClient) {}

    create(song: ISong): Observable<EntityResponseType> {
        const copy = this.convertDateFromClient(song);
        return this.http.post<ISong>(this.resourceUrl,
                 copy ,
                { observe: 'response' })
        .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
    }

    update(song: ISong): Observable<EntityResponseType> {
        const copy = this.convertDateFromClient(song);
        return this.http.put<ISong>(this.resourceUrl,
                 copy ,
                { observe: 'response' })
        .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
    }

    find(id: number): Observable<EntityResponseType> {
        return this.http.get<ISong>(`${this.resourceUrl}/${id}`, { observe: 'response' })
            .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
    }

    query(req?: any): Observable<EntityArrayResponseType> {
        const options = createRequestOption(req);
        return this.http.get<ISong[]>(this.resourceUrl, { params: options, observe: 'response' })
            .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
    }

    delete(id: number): Observable<HttpResponse<{}>> {
        return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
    }


    protected convertDateFromClient(song: ISong): ISong {
        const copy: ISong = Object.assign({}, song, {
        releaseDate: song.releaseDate?.isValid() ? song.releaseDate.format(DATE_FORMAT) : undefined,
        });
        return copy;
    }

    protected convertDateFromServer(res: EntityResponseType): EntityResponseType {
        if (res.body) {
            res.body.releaseDate = res.body.releaseDate ? dayjs(res.body.releaseDate) : undefined;
        }
        return res;
    }

    protected convertDateArrayFromServer(res: EntityArrayResponseType): EntityArrayResponseType {
        if (res.body) {
            res.body.forEach ((song: ISong) => {
                song.releaseDate = song.releaseDate ? dayjs(song.releaseDate) : undefined;
            });
        }
        return res;
    }
}
