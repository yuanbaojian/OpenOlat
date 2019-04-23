alter table o_bs_identity add column deleteddate datetime;
alter table o_bs_identity add column deletedroles varchar(1024);
alter table o_bs_identity add column deletedby varchar(128);


alter table o_loggingtable drop username, drop userproperty1, drop userproperty2, drop userproperty3, drop userproperty4, drop userproperty5, drop userproperty6, drop userproperty7, drop userproperty8, drop userproperty9, drop userproperty10, drop userproperty11, drop userproperty12;

update o_bs_identity set name=concat('del_',id) where status=199;

update o_user inner join o_bs_identity on (id=fk_identity) set u_firstname=null where status=199;
update o_user inner join o_bs_identity on (id=fk_identity) set u_lastname=null where status=199;
update o_user inner join o_bs_identity on (id=fk_identity) set u_email=null where status=199;
update o_user inner join o_bs_identity on (id=fk_identity) set u_birthday=null where status=199;
update o_user inner join o_bs_identity on (id=fk_identity) set u_graduation=null where status=199;
update o_user inner join o_bs_identity on (id=fk_identity) set u_gender=null where status=199;
update o_user inner join o_bs_identity on (id=fk_identity) set u_telprivate=null where status=199;
update o_user inner join o_bs_identity on (id=fk_identity) set u_telmobile=null where status=199;
update o_user inner join o_bs_identity on (id=fk_identity) set u_teloffice=null where status=199;
update o_user inner join o_bs_identity on (id=fk_identity) set u_skype=null where status=199;
update o_user inner join o_bs_identity on (id=fk_identity) set u_msn=null where status=199;
update o_user inner join o_bs_identity on (id=fk_identity) set u_xing=null where status=199;
update o_user inner join o_bs_identity on (id=fk_identity) set u_icq=null where status=199;
update o_user inner join o_bs_identity on (id=fk_identity) set u_homepage=null where status=199;
update o_user inner join o_bs_identity on (id=fk_identity) set u_street=null where status=199;
update o_user inner join o_bs_identity on (id=fk_identity) set u_extendedaddress=null where status=199;
update o_user inner join o_bs_identity on (id=fk_identity) set u_pobox=null where status=199;
update o_user inner join o_bs_identity on (id=fk_identity) set u_zipcode=null where status=199;
update o_user inner join o_bs_identity on (id=fk_identity) set u_region=null where status=199;
update o_user inner join o_bs_identity on (id=fk_identity) set u_city=null where status=199;
update o_user inner join o_bs_identity on (id=fk_identity) set u_country=null where status=199;
update o_user inner join o_bs_identity on (id=fk_identity) set u_countrycode=null where status=199;
update o_user inner join o_bs_identity on (id=fk_identity) set u_institutionalname=null where status=199;
update o_user inner join o_bs_identity on (id=fk_identity) set u_institutionaluseridentifier=null where status=199;
update o_user inner join o_bs_identity on (id=fk_identity) set u_institutionalemail=null where status=199;
update o_user inner join o_bs_identity on (id=fk_identity) set u_orgunit=null where status=199;
update o_user inner join o_bs_identity on (id=fk_identity) set u_studysubject=null where status=199;
update o_user inner join o_bs_identity on (id=fk_identity) set u_emchangekey=null where status=199;
update o_user inner join o_bs_identity on (id=fk_identity) set u_emaildisabled=null where status=199;
update o_user inner join o_bs_identity on (id=fk_identity) set u_typeofuser=null where status=199;
update o_user inner join o_bs_identity on (id=fk_identity) set u_socialsecuritynumber=null where status=199;
update o_user inner join o_bs_identity on (id=fk_identity) set u_genericselectionproperty=null where status=199;
update o_user inner join o_bs_identity on (id=fk_identity) set u_genericselectionproperty2=null where status=199;
update o_user inner join o_bs_identity on (id=fk_identity) set u_genericselectionproperty3=null where status=199;
update o_user inner join o_bs_identity on (id=fk_identity) set u_generictextproperty=null where status=199;
update o_user inner join o_bs_identity on (id=fk_identity) set u_generictextproperty2=null where status=199;
update o_user inner join o_bs_identity on (id=fk_identity) set u_generictextproperty3=null where status=199;
update o_user inner join o_bs_identity on (id=fk_identity) set u_generictextproperty4=null where status=199;
update o_user inner join o_bs_identity on (id=fk_identity) set u_generictextproperty5=null where status=199;
update o_user inner join o_bs_identity on (id=fk_identity) set u_genericuniquetextproperty=null where status=199;
update o_user inner join o_bs_identity on (id=fk_identity) set u_genericuniquetextproperty2=null where status=199;
update o_user inner join o_bs_identity on (id=fk_identity) set u_genericuniquetextproperty3=null where status=199;
update o_user inner join o_bs_identity on (id=fk_identity) set u_genericemailproperty1=null where status=199;
update o_user inner join o_bs_identity on (id=fk_identity) set u_genericcheckboxproperty=null where status=199;
update o_user inner join o_bs_identity on (id=fk_identity) set u_genericcheckboxproperty2=null where status=199;
update o_user inner join o_bs_identity on (id=fk_identity) set u_genericcheckboxproperty3=null where status=199;
update o_user inner join o_bs_identity on (id=fk_identity) set u_rank=null where status=199;
update o_user inner join o_bs_identity on (id=fk_identity) set u_degree=null where status=199;
update o_user inner join o_bs_identity on (id=fk_identity) set u_position=null where status=199;
update o_user inner join o_bs_identity on (id=fk_identity) set u_userinterests=null where status=199;
update o_user inner join o_bs_identity on (id=fk_identity) set u_usersearchedinterests=null where status=199;
update o_user inner join o_bs_identity on (id=fk_identity) set u_officestreet=null where status=199;
update o_user inner join o_bs_identity on (id=fk_identity) set u_extendedofficeaddress=null where status=199;
update o_user inner join o_bs_identity on (id=fk_identity) set u_officepobox=null where status=199;
update o_user inner join o_bs_identity on (id=fk_identity) set u_officezipcode=null where status=199;
update o_user inner join o_bs_identity on (id=fk_identity) set u_officecity=null where status=199;
update o_user inner join o_bs_identity on (id=fk_identity) set u_officecountry=null where status=199;
update o_user inner join o_bs_identity on (id=fk_identity) set u_officemobilephone=null where status=199;
update o_user inner join o_bs_identity on (id=fk_identity) set u_department=null where status=199;
update o_user inner join o_bs_identity on (id=fk_identity) set u_privateemail=null where status=199;
update o_user inner join o_bs_identity on (id=fk_identity) set u_edupersonaffiliation=null where status=199;
update o_user inner join o_bs_identity on (id=fk_identity) set u_swissedupersonhomeorg=null where status=199;
update o_user inner join o_bs_identity on (id=fk_identity) set u_swissedupersonstudylevel=null where status=199;
update o_user inner join o_bs_identity on (id=fk_identity) set u_swissedupersonhomeorgtype=null where status=199;
update o_user inner join o_bs_identity on (id=fk_identity) set u_employeenumber=null where status=199;
update o_user inner join o_bs_identity on (id=fk_identity) set u_swissedupersonstaffcategory=null where status=199;
update o_user inner join o_bs_identity on (id=fk_identity) set u_organizationalunit=null where status=199;
update o_user inner join o_bs_identity on (id=fk_identity) set u_swissedupersonstudybranch1=null where status=199;
update o_user inner join o_bs_identity on (id=fk_identity) set u_swissedupersonstudybranch2=null where status=199;
update o_user inner join o_bs_identity on (id=fk_identity) set u_swissedupersonstudybranch3=null where status=199;

drop table o_stat_homeorg;
drop table o_stat_orgtype;
drop table o_stat_studylevel;
drop table o_stat_studybranch3;

-- user data export
create table o_user_data_export (
   id bigint not null auto_increment,
   creationdate datetime,
   lastmodified datetime,
   u_directory varchar(255),
   u_status varchar(16),
   u_export_ids varchar(2000),
   fk_identity bigint not null,
   fk_request_by bigint,
   primary key (id)
);
alter table o_user_data_export ENGINE = InnoDB;

alter table o_user_data_export add constraint usr_dataex_to_ident_idx foreign key (fk_identity) references o_bs_identity (id);
alter table o_user_data_export add constraint usr_dataex_to_requ_idx foreign key (fk_request_by) references o_bs_identity (id);




