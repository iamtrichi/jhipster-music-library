
import * as dayjs from 'dayjs';
import { IAlbum } from 'app/entities/album/album.model';

export interface ISong {
    id?: number;
    name?: string;
    url?: string;
    releaseDate?: dayjs.Dayjs;
    album?: IAlbum;
}

export class Song implements ISong {
    constructor(
        public id?: number,
        public name?: string,
        public url?: string,
        public releaseDate?: dayjs.Dayjs,
        public album?: IAlbum,
    ) {
    }
}
