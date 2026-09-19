import axios from 'axios';
export const api=axios.create({baseURL:'http://localhost:8080/api'});
api.interceptors.request.use(c=>{const t=localStorage.getItem('token');if(t)c.headers.Authorization=`Bearer ${t}`;return c;});
export const login=(email:string,password:string)=>api.post('/auth/login',{email,password});
