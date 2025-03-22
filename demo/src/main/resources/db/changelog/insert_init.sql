-- Default permissions init
truncate table default_permission cascade;
INSERT INTO default_permission(name, description)
VALUES ('PRODUCT_GROUP_CREATE', 'Product group create permission'),
       ('PRODUCT_GROUP_UPDATE', 'Product group update permission'),
       ('PRODUCT_GROUP_DELETE', 'Product group delete permission'),
       ('PRODUCT_GROUP_GET_BY_ID', 'Product group get by id permission'),
       ('PRODUCT_GROUP_GET_BY_CODE', 'Product group get by code permission'),
       ('PRODUCT_GROUP_GET_BY_PARENT_CODE', 'Product group get by parent code permission'),
       ('PRODUCT_GROUP_GET_LIST', 'Product group get list permission'),

       ('PRODUCT_PROPERTY_CREATE', 'Product property create permission'),
       ('PRODUCT_PROPERTY_UPDATE', 'Product property update permission'),
       ('PRODUCT_PROPERTY_GET_BY_ID', 'Product property get by id permission'),
       ('PRODUCT_PROPERTY_GET_BY_NAME', 'Product property get by name permission'),
       ('PRODUCT_PROPERTY_GET_BY_NOMENCLATURE_ID_WITH_SEARCH',
        'Product property get by nomenclature id and with search param permission'),

       ('UNITS_CREATE', 'Unit create permission'),
       ('UNITS_UPDATE', 'Unit update permission'),
       ('UNITS_DELETE', 'Unit delete permission'),
       ('UNITS_GET_BY_ID', 'Unit get by id permission'),
       ('UNITS_GET_LIST', 'Unit get list permission'),

       ('VIEW_OF_NOMENCLATURE_CREATE', 'View of nomenclature create permission'),
       ('VIEW_OF_NOMENCLATURE_UPDATE', 'View of nomenclature update permission'),
       ('VIEW_OF_NOMENCLATURE_DELETE', 'View of nomenclature delete permission'),
       ('VIEW_OF_NOMENCLATURE_GET_BY_ID', 'View of nomenclature get by id permission'),
       ('VIEW_OF_NOMENCLATURE_GET_BY_GROUP_ID', 'View of nomenclature get list by group id permission'),
       ('VIEW_OF_NOMENCLATURE_GET_LIST', 'View of nomenclature get list permission'),

       ('ROLE_UPDATE', 'Update role permission'),
       ('ROLE_LIST', 'Get list of roles permission'),
       ('ROLE_ALL_ENUMS', 'Get all role enums permission'),
       ('ROLE_DELETE', 'Delete role by id permission'),

       ('USER_CREATE', 'Create user permission'),
       ('USER_UPDATE', 'Update user permission'),
       ('USER_LIST', 'Get user list permission'),
       ('USER_BY_ID', 'Get user by id permission'),

       ('PRODUCT_ADD', 'Add product permission'),
       ('PRODUCT_UPDATE', 'Update product permission'),
       ('PRODUCT_DELETE', 'Delete product permission'),
       ('PRODUCT_GET_BY_ID', 'Get product by id permission'),
       ('PRODUCT_GET_AS_PAGE', 'Get product as page permission'),
       ('PRODUCT_GET_BY_GTIN', 'Get product by GTIN permission'),

       ('CONVERSION_GET_BY_FROM_ID_AND_TO_ID', 'Get conversion by fromUnitId and toUnitId permission'),
       ('CONVERSION_DELETE', 'Delete conversion permission'),
       ('CONVERSION_UPDATE', 'Update conversion permission'),
       ('CONVERSION_ADD', 'Add conversion permission'),
       ('CONVERSION_GET_BY_MAIN_UNIT_ID', 'Get conversion by main unit id permission'),

       ('GTIN_TO_PRODUCT_ADD', 'Add GTIN to product permission'),
       ('GTIN_TO_PRODUCT_UPDATE', 'Update GTIN or product permission'),
       ('GTIN_TO_PRODUCT_GET_BY_ID', 'Get GTIN by id permission'),
       ('GTIN_TO_PRODUCT_GET_LIST_BY_PRODUCT_ID', 'Get list of GTIN by product id permission'),
       ('GTIN_TO_PRODUCT_DELETE', 'Delete GTIN by id permission'),

       ('SERIES_CREATE', 'Create Series permission'),
       ('SERIES_UPDATE', 'Update Series permission'),
       ('SERIES_DELETE', 'Delete Series permission'),
       ('SERIES_GET_BY_ID', 'Get Series by id permission'),
       ('SERIES_GET_AS_PAGE', 'Get Series as pageable permission'),
       ('SERIES_GET_BY_PRODUCT_ID', 'Get Series by product id permission'),

       ('SETTING_CREATE', 'Create setting permission'),
       ('SETTING_UPDATE', 'Update setting permission'),
       ('SETTING_DELETE', 'Delete setting permission'),
       ('SETTING_GET_BY_ID', 'Get setting by id permission'),

       ('MARKING_CODE_ORDER', 'Ordering Marking code permission'),

       ('PERMISSION_GET_BY_USER_ID', 'Get user permissions permission'),

       ('FACTORY_CREATE', 'Factory create permission'),
       ('FACTORY_UPDATE', 'Factory update permission'),
       ('FACTORY_GET_BY_ID', 'Factory get by id permission'),
       ('FACTORY_GET_BY_NAME', 'Factory get by name permission'),
       ('FACTORY_GET_LIST_AS_PAGE', 'Factory get list as page permission'),
       ('FACTORY_DELETE', 'Factory delete by id permission'),

       ('DEPARTMENT_CREATE', 'Department create permission'),
       ('DEPARTMENT_UPDATE', 'Department update permission'),
       ('DEPARTMENT_GET_BY_ID', 'Department get by id permission'),
       ('DEPARTMENT_GET_LIST_AS_PAGE', 'Department get list as page permission'),
       ('DEPARTMENT_DELETE', 'Department delete by id permission'),

       ('EMPLOYEE_CREATE', 'Create employee permission'),
       ('EMPLOYEE_UPDATE', 'Update employee by id permission'),
       ('EMPLOYEE_DELETE', 'Delete employee by id permission'),
       ('EMPLOYEE_GET_BY_ID', 'Get employee by id permission'),
       ('EMPLOYEE_GET_LIST_AS_PAGE', 'Get employee as pageable permission'),

       ('TERMINAL_CREATE', 'Terminal create permission'),
       ('TERMINAL_UPDATE', 'Terminal update permission'),
       ('TERMINAL_DELETE', 'Terminal delete permission'),
       ('TERMINAL_GET_BY_ID', 'Get Terminal by id permission'),
       ('TERMINAL_GET_LIST_AS_PAGE', 'Get Terminal as pageable permission'),
       ('TERMINAL_ATTACH_TO_LINE', 'Attach Terminal to line permission'),

       ('SMART_CODE_READER_KM', 'Smart code reader api permission'),
       ('FINISH_AGGREGATION_MANUALLY', 'Finish aggregation manually api permission'),

       ('LINE_CREATE', 'Create line permission'),
       ('LINE_UPDATE', 'Update line permission'),
       ('LINE_DELETE', 'Delete line permission'),
       ('LINE_GET_BY_ID', 'Get line by id permission'),
       ('LINE_GET_LIST_AS_PAGE', 'Get line as pageable permission'),

       ('AGGREGATION_GET_BY_ID', 'Get Aggregation by id permission'),
       ('AGGREGATION_AS_PAGE', 'Get Aggregation as pageable permission'),

       ('TASK_CREATE', 'Create task permission'),
       ('TASK_UPDATE', 'Update task permission'),
       ('TASK_GET_BY_ID', 'Get task by id permission'),
       ('TASK_GET_LIST_AS_PAGE', 'Get Task as pageable permission'),
       ('TASK_DELETE', 'Delete task permission'),

       ('WAREHOUSE_CREATE', 'Create warehouse permission'),
       ('WAREHOUSE_UPDATE', 'Update warehouse permission'),
       ('WAREHOUSE_DELETE', 'Delete warehouse permission'),
       ('WAREHOUSE_GET_BY_ID', 'Get warehouse by id permission'),
       ('WAREHOUSE_GET_LIST_AS_PAGE_AND_SEARCH', 'Get warehouse list as pageable permission'),

       ('PROCESS_CREATE', 'Create process permission'),
       ('PROCESS_UPDATE', 'Update process permission'),
       ('PROCESS_GET', 'Get process permission'),
       ('PROCESS_DELETE', 'Delete process permission'),

       ('PROCESS_CHAIN_CREATE', 'Create process chain permission'),
       ('PROCESS_CHAIN_GET', 'Get process chain permission'),
       ('PROCESS_CHAIN_UPDATE', 'Update process chain permission'),
       ('PROCESS_CHAIN_DELETE', 'Delete process chain permission'),

       ('TASK_PROCESSOR_GET_BY_SERIES', 'Get task processor by series permission'),
       ('TASK_PROCESSOR_GET_BY_DEPARTMENT', 'Get task processor by department permission');


-- Add role default permissions
truncate table role_default_permissions cascade;
INSERT INTO role_default_permissions (default_permission_name, role_id)
SELECT dp.name, r.name
FROM roles r
         CROSS JOIN default_permission dp
where r.name like '%' || 'ROLE_ADMIN' || '%'
   or r.name like '%' || 'ROLE_USER' || '%';

-- add roles
truncate table roles cascade;
insert into roles(name, privilege)
values ('ROLE_ADMIN', 0),
       ('ROLE_USER', 10);

-- add user
truncate table user_entity cascade;
INSERT INTO user_entity(id, username, password, created_at, updated_at, is_active, is_blocked, attempts, status,
                  encrypted_code)
VALUES (1, 'bingo', '$2a$10$Fgao4CtX91jkbLZlXKUGFOfnPFtfaS352yOqxAXJuoV8ZYrcT5f5q', now(), now(), false, false, 0,
        'CREATED', null);

-- add roles to user
truncate table user_roles cascade;
insert into user_roles(role_id, user_id)
values ('ROLE_ADMIN', 1),
       ('ROLE_USER', 1);
-- add warehouse
truncate table warehouse cascade;
insert into warehouse (id, created_at, updated_at, status, created_by, updated_by, name, description, address)
VALUES (1, now(), now(), 'CREATED', 1, 1, 'Warehouse test', 'desc', 'test address');
-- add factory
truncate table factory cascade;
insert into factory(id, created_at, updated_at, status, created_by, updated_by, name, description, default_warehouse_id)
VALUES (1, now(), now(), 'CREATED', 1, 1, 'Factory test', 'desc', 1);

--add department
truncate table department cascade;
insert into department (id, created_at, updated_at, status, created_by, updated_by, name, description,
                        factory_id)
values (1, now(), now(), 'CREATED', 1, 1, 'Department test', 'desc', 1);

-- add user permission
truncate table user_permission cascade;
insert into user_permission (id, created_at, updated_at, status, created_by, updated_by, user_id, name)
select row_number() over (),
       now(),
       now(),
       'CREATED',
       1,
       1,
       1,
       name
from default_permission