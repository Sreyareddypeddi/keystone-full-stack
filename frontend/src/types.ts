export type Role='MANAGER'|'DISPATCHER'|'TECHNICIAN'|'CUSTOMER';
export interface WO{id:number;code:string;title:string;description?:string;priority:string;status:string;customerId:number;customerName:string;siteId:number;siteName:string;technicianId?:number;technicianName?:string;slaDueAt:string;overdue:boolean;history:any[]}
