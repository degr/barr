import React from 'react';
import * as THREE from 'three';
import CANNON from 'cannon';
import PointerControl from "./PointerControl";
import FBXLoader from 'three-fbxloader-offical';
import './Canvas.scss';
import {register} from "../kurrento/conferenceroom";

const pathBarAvatar = '/models/fbx/avatar_sit_bar_01.fbx';
const pathTableAvatar = '/models/fbx/avatar_sit_table_01.fbx';
const pathSofaAvatar = '/models/fbx/avatar_sit_sofa_01.fbx';
let scene = new THREE.Scene();
let loader = new FBXLoader();
let mixer_01, avatar_01, avatar_pos;

function loadAvatar(path, pos, clb) {
    loader.load(path, (avatar) => {
        avatar.traverse((object) => {
            object.frustumCulled = false;
        });
        avatar.traverse((child) => {
            if (child.isMesh) {
                child.castShadow = true;
            }
        });
        avatar.traverse((node) => {
            if (node.material) {
                node.material.side = THREE.DoubleSide;
            }
        });

        const mixer = new THREE.AnimationMixer(avatar);
        let action = mixer.clipAction(avatar.animations[0]);
        action.play();

        avatar_pos = pos;
        avatar.position.set(pos["x"], pos["y"], pos["z"]);
        avatar.scale.set(0.1, 0.1, 0.1);
        avatar.rotateY = Math.PI;
        clb(mixer, avatar);
    });
}

let settings = {enabled: false};

export default class Canvas extends React.Component {
    constructor(props) {
        super(props);
        this.pointerControl = null;
        this.avatarIsLoaded = false;
        this.state = {
            showInstruction: true
        }
    }

    componentDidMount() {
        const me = this;
        let sphereShape, sphereBody, world;

        let camera, renderer;
        let controls, time = Date.now();

        let meshPlanet, geometry;
        let rotationSpeed = 0.02;
        let textureLoader = new THREE.TextureLoader();

        initCannon();
        initScene();
        animate();


        function initCannon() {
            // Setup our world
            world = new CANNON.World();
            world.quatNormalizeSkip = 0;
            world.quatNormalizeFast = false;

            let solver = new CANNON.GSSolver();

            world.defaultContactMaterial.contactEquationStiffness = 1e9;
            world.defaultContactMaterial.contactEquationRelaxation = 4;

            solver.iterations = 7;
            solver.tolerance = 0.1;
            let split = true;
            if (split)
                world.solver = new CANNON.SplitSolver(solver);
            else
                world.solver = solver;

            world.gravity.set(0, -8, 0);
            world.broadphase = new CANNON.NaiveBroadphase();


            // Create a sphere
            let mass = 1, radius = 1.4;
            sphereShape = new CANNON.Sphere(radius);
            sphereBody = new CANNON.Body({mass: mass});
            sphereBody.addShape(sphereShape);
            sphereBody.position.set(0, 0, 0);
            sphereBody.quaternion.setFromEuler(Math.PI / 2, 0, 0);
            sphereBody.linearDamping = 0.9;
            world.add(sphereBody);

            // Create a plane
            let groundShape = new CANNON.Plane();
            let groundBody = new CANNON.Body({mass: 0});
            groundBody.addShape(groundShape);
            groundBody.quaternion.setFromAxisAngle(new CANNON.Vec3(1, 0, 0), -Math.PI / 2);
            world.add(groundBody);
        }


        function createSpotlight(color) {
            let newObj = new THREE.SpotLight(color, 1);
            newObj.castShadow = true;
            newObj.angle = 1.2;
            newObj.penumbra = 0.1;
            newObj.decay = 0.1;
            newObj.distance = 15;
            newObj.shadow.mapSize.width = 512;
            newObj.shadow.mapSize.height = 512;
            return newObj;
        }

        function createSpotlight2(color) {
            let newObj = new THREE.SpotLight(color, 1);
            newObj.castShadow = true;
            newObj.angle = 0.63;
            newObj.penumbra = 0.3;
            newObj.decay = 0.1;
            newObj.distance = 7;
            newObj.shadow.mapSize.width = 512;
            newObj.shadow.mapSize.height = 512;
            return newObj;
        }

        function createSpotlight3(color) {
            let newObj = new THREE.SpotLight(color, 1);
            newObj.castShadow = true;
            newObj.angle = 0.9;
            newObj.penumbra = 0.09;
            newObj.decay = 0.1;
            newObj.distance = 7;
            newObj.shadow.mapSize.width = 512;
            newObj.shadow.mapSize.height = 512;
            return newObj;
        }

        function initScene() {

            camera = new THREE.PerspectiveCamera(45, window.innerWidth / window.innerHeight, 0.1, 1000);

            //scene.background = new THREE.Color( 0x000000 );
            scene.fog = new THREE.Fog(0x00C3FF, 2, 500);

            let ambient = new THREE.AmbientLight(0x00779b, 0.43);
            scene.add(ambient);

            controls = new PointerControl(camera, sphereBody, settings);
            me.pointerControl = controls;
            scene.add(controls.getObject());


            let materialNormalMap = new THREE.MeshPhongMaterial({
                specular: 0x333333,
                shininess: 15,
                map: textureLoader.load("/models/fbx/earth_atmos_2048.jpg"),
                specularMap: textureLoader.load("/models/fbx/earth_specular_2048.jpg"),
                normalMap: textureLoader.load("/models/fbx/earth_normal_2048.jpg"),
                normalScale: new THREE.Vector2(0.85, 0.85)
            });
            // planet
            geometry = new THREE.SphereBufferGeometry(radius, 100, 50);

            meshPlanet = new THREE.Mesh(geometry, materialNormalMap);
            meshPlanet.position.set(0, 25, 75);
            meshPlanet.scale.set(20, 20, 20);
            meshPlanet.rotation.y = Math.PI / 3.4;
            meshPlanet.rotation.x = -Math.PI / 3;
            scene.add(meshPlanet);

            //region Stars
            let radius = 10;
            let i, r = radius, starsGeometry = [new THREE.BufferGeometry(), new THREE.BufferGeometry()];
            let vertices1 = [];
            let vertices2 = [];
            let vertex = new THREE.Vector3();
            for (i = 0; i < 250; i++) {
                vertex.x = Math.random() * 2 - 1;
                vertex.y = Math.random() * 2 - 1;
                vertex.z = Math.random() * 2 - 1;
                vertex.multiplyScalar(r);
                vertices1.push(vertex.x, vertex.y, vertex.z);
            }
            for (i = 0; i < 1500; i++) {
                vertex.x = Math.random() * 2 - 1;
                vertex.y = Math.random() * 2 - 1;
                vertex.z = Math.random() * 2 - 1;
                vertex.multiplyScalar(r);
                vertices2.push(vertex.x, vertex.y, vertex.z);
            }
            starsGeometry[0].addAttribute('position', new THREE.Float32BufferAttribute(vertices1, 3));
            starsGeometry[1].addAttribute('position', new THREE.Float32BufferAttribute(vertices2, 3));
            let stars;
            let starsMaterials = [
                new THREE.PointsMaterial({color: 0x555555, size: 2, sizeAttenuation: false}),
                new THREE.PointsMaterial({color: 0x555555, size: 1, sizeAttenuation: false}),
                new THREE.PointsMaterial({color: 0x333333, size: 2, sizeAttenuation: false}),
                new THREE.PointsMaterial({color: 0x3a3a3a, size: 1, sizeAttenuation: false}),
                new THREE.PointsMaterial({color: 0x1a1a1a, size: 2, sizeAttenuation: false}),
                new THREE.PointsMaterial({color: 0x1a1a1a, size: 1, sizeAttenuation: false})
            ];
            for (i = 10; i < 30; i++) {
                stars = new THREE.Points(starsGeometry[i % 2], starsMaterials[i % 6]);
                stars.rotation.x = Math.random() * 6;
                stars.rotation.y = Math.random() * 6;
                stars.rotation.z = Math.random() * 6;
                stars.scale.setScalar(i * 10);
                stars.matrixAutoUpdate = false;
                stars.updateMatrix();
                scene.add(stars);
            }
            //endregion

            //region Bar
            loader.load('/models/fbx/lightBulbs_grp_01.fbx', (lightBulbs) => {
                scene.add(lightBulbs);
            });

            loader.load('/models/fbx/floor.fbx', (floor) => {
                floor.traverse(function (child) {
                    if (child.isMesh) {
                        floor.scale.set(0.1, 0.1, 0.1);
                        child.receiveShadow = true;
                    }
                });
                scene.add(floor);
            });

            loader.load('/models/fbx/buttles_grp_01.fbx', (bottles) => {
                bottles.traverse((node) => {
                    if (node.material) {
                        node.material.side = THREE.DoubleSide;
                    }
                });

                bottles.traverse((child) => {
                    if (child.isMesh) {
                        //child.castShadow = true;
                        bottles.scale.set(0.1, 0.1, 0.1);
                    }
                });
                scene.add(bottles);
            });

            loader.load('/models/fbx/logo_02.fbx', (logo) => {
                scene.add(logo);
                logo.scale.set(0.1, 0.1, 0.1);
            });

            loader.load('/models/fbx/bar_final_03.fbx', (bar) => {
                bar.traverse((child) => {
                    if (child.isMesh) {
                        bar.scale.set(0.1, 0.1, 0.1);
                        child.castShadow = true;
                        child.receiveShadow = true;
                    }
                });
                scene.add(bar);
            });

            loader.load('/models/fbx/tv.fbx', (tv) => {
                tv.traverse((child) => {
                    if (child.isMesh) {
                        tv.scale.set(0.1, 0.1, 0.1);
                    }
                });
                scene.add(tv);
            });

            loader.load('/models/fbx/tarshers_grp.fbx', (tarshers) => {
                tarshers.traverse((child) => {
                    if (child.isMesh) {
                        child.castShadow = true;

                    }
                });
                scene.add(tarshers);
                tarshers.scale.set(0.1, 0.1, 0.1);
            });

            loader.load('/models/fbx/bar_chairs_grp.fbx', (chairs) => {
                chairs.traverse((child) => {
                    if (child.isMesh) {
                        child.castShadow = true;
                        chairs.scale.set(0.1, 0.1, 0.1);
                    }
                });
                scene.add(chairs);
            });
            //endregion

            //region SpotLights
            let spotLight1 = createSpotlight(0xFF7E00);
            spotLight1.position.set(0, 2.4, -1);
            spotLight1.target.position.set(0, 0, -1.3);
            //lightHelper1 = new THREE.SpotLightHelper( spotLight1 );
            /*            spotLight1.shadow.camera.near = 10;
                        spotLight1.shadow.camera.far = 1;
                        spotLight1.shadow.camera.fov = 1;*/
            scene.add(spotLight1.target, spotLight1);
            //scene.add( lightHelper1);

            let spotLight2 = createSpotlight2(0xFF7E00);
            spotLight2.position.set(-4.48, 2.4, 2.7);
            spotLight2.target.position.set(-4.48, 0, 2.7);
            //lightHelper2 = new THREE.SpotLightHelper( spotLight2 );
            scene.add(spotLight2.target, spotLight2);
            //scene.add( lightHelper2);

            let spotLight3 = createSpotlight3(0xFF7E00);
            spotLight3.position.set(3.84, 2.4, 4.5);
            spotLight3.target.position.set(3.84, 0, 4.5);
            //lightHelper3 = new THREE.SpotLightHelper( spotLight3 );
            scene.add(spotLight3.target, spotLight3);
            //scene.add( lightHelper3);
            //endregion

            //region Render
            renderer = new THREE.WebGLRenderer({antialias: true});
            renderer.shadowMap.enabled = true;
            //renderer.setPixelRatio( window.devicePixelRatio ); //from fbx_load
            renderer.shadowMapSoft = true;
            renderer.shadowMap.type = THREE.PCFSoftShadowMap;
            renderer.setSize(window.innerWidth, window.innerHeight);
            renderer.setClearColor(scene.fog.color, 0.1);
            //endregion

            document.body.appendChild(renderer.domElement);
            renderer.domElement.addEventListener('click', () => {
                settings.enabled = !settings.enabled;
            });
            window.addEventListener('resize', onWindowResize, false);
        }

        function onWindowResize() {
            camera.aspect = window.innerWidth / window.innerHeight;
            camera.updateProjectionMatrix();
            renderer.setSize(window.innerWidth, window.innerHeight);
        }

        const dt = 1 / 60;

        function animate() {
            requestAnimationFrame(animate);

            if (avatar_01 && avatar_pos) {
                avatar_01.rotation.y = avatar_pos["a"];
            }

            if (settings.enabled) {
                world.step(dt);
                meshPlanet.rotation.y += rotationSpeed * dt;
                if (mixer_01) mixer_01.update(dt);

            }
            controls.update(Date.now() - time);
            renderer.render(scene, camera);
            time = Date.now();
        }
    }

    render() {
        return <div/>
    }

    componentDidUpdate(prevProps, prevState, snapshot) {
        if (prevProps.location !== this.props.location) {
            if (!this.avatarIsLoaded) {
                let type = this.props.location.type;
                let path;
                switch (type) {
                    case 'bar': {
                        path = pathBarAvatar;
                        break
                    }
                    case 'sofa': {
                        path = pathSofaAvatar;
                        break
                    }
                    case 'table1': {
                        path = pathTableAvatar;
                        break
                    }
                    case 'table2': {
                        path = pathTableAvatar;
                        break
                    }
                    default : {
                        path = pathBarAvatar;
                        break;
                    }
                }
                register();
                scene.remove(avatar_01);
                loadAvatar(path, this.props.location, function (mixer, avatar) {
                    mixer_01 = mixer;
                    avatar_01 = avatar;
                    scene.add(avatar);
                    this.avatarIsLoaded = true;
                });
            }
            const object = this.props.location;
            this.pointerControl.setPosition(object.x, object.y + 1.5, object.z);
        }
    }
}