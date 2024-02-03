from setuptools import setup, find_packages

setup(
    name='myproject',
    version='1.0',
    packages=find_packages(),
    install_requires=[],
    entry_points={
        'console_scripts': [
            'myproject = main.python.main:hello_world',
        ],
    },
    test_suite='tests.main.python',
)
