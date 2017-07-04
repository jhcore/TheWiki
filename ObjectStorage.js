const concat = require('concat-stream'); 
const pkgcloud = require('pkgcloud');
const http = require('http'); 
const fs = require('fs'); 

class ObjectStorage {
    constructor(){
        this.config = {
            provider: 'openstack',
            useServiceCatalog: true,
            useInternal: false,
            keystoneAuthVersion: 'v3',
            authUrl: 'https://lon-identity.open.softlayer.com',
            tenantId: 'projectId',     
            domainId: 'domainId',
            username: 'username',
            password: 'password',
            region: 'region' 
        }
        this.storageClient = pkgcloud.storage.createClient(this.config); 
        this.storageClient.auth(err => {if (err) console.error(err)});
    } 
    
    saveInObjectStorage(path, databaseName) {
        var me = this;
        this.storageClient.createContainer({
            name: databaseName
        }, 
        function (err, container) {
            if (err) {
                console.error(err);
            }
            else {  
                var upload = me.storageClient.upload({
                    container: container.name,
                    remote: path
                });

                upload.on('error', function(err) {
                    console.error(err);
                });

                var file = fs.createReadStream(path);
                file.pipe(upload);
            }
        });
    } 
}
 
module.exports = ObjectStorage;    
