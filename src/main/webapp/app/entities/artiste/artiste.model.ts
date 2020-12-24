
import * as dayjs from 'dayjs';
import { IAlbum } from 'app/entities/album/album.model';

export interface IArtiste {
    id?: number;
    firstName?: string;
    lastName?: string;
    dateNaiss?: dayjs.Dayjs;
    makes?: IAlbum[];
}

export class Artiste implements IArtiste {
    constructor(
        public id?: number,
        public firstName?: string,
        public lastName?: string,
        public dateNaiss?: dayjs.Dayjs,
        public makes?: IAlbum[],
    ) {
    }
}
